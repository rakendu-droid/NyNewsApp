package dev.rrj.com.nynewsapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NewsListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ListView listView;
    ArrayList dataList;
    NewsAdapter adapter;
    Context ctx;
    View loadMoreView;
    boolean loadingMore =false;
    final String BASE_URL = "http://api.nytimes.com/svc/mostpopular/v2/mostemailed/all-sections/1.json?api-key=fa5723452d7d2454cf24a2a3d920012c:10:66680873";
    String url;

    public NewsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        dataList = new ArrayList<NewsModel>();
        adapter = new NewsAdapter(getActivity(),dataList);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setRetainInstance(true);
        View view =  inflater.inflate(R.layout.fragment_list, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                NewsModel newsObj =  (NewsModel)dataList.get(position);
                if(mListener!=null){
                    mListener.onFragmentInteraction(newsObj.getUrl());
                }

            }

        });

        loadMoreView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loadmore,null,false);
        listView.addFooterView(loadMoreView);
        listView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


                int lastitem =  (firstVisibleItem+visibleItemCount);

                if((lastitem == totalItemCount) && !(loadingMore)){

                    String url = BASE_URL+"&offset="+(lastitem-1);

                    new NewsDownloaderTask().execute(url);
                }

            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(String uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String uri);
    }



    private  class NewsDownloaderTask extends AsyncTask<String,Void,ArrayList<NewsModel>> {

        protected void onPreExecute(){
            // Toast.makeText(ctx,"Loading more",Toast.LENGTH_LONG).show();
            loadingMore =true;
        }
        @Override
        protected ArrayList<NewsModel> doInBackground(String... params) {
            String url = params[0];

            return getNews(url);

        }

        protected void onPostExecute(ArrayList newsList) {

            loadingMore =false;

            for(int i=0;i<newsList.size();i++)
            {
                dataList.add(newsList.get(i));

            }
            adapter.change();

        }

        private ArrayList<NewsModel> getNews(String link) {

            String response =  new String();
            try {
                URL url = new URL(link);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if(statusCode != HttpStatus.SC_OK)
                {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                response = readStream(inputStream);



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return parseResult(response);
        }

        private String readStream(InputStream inputStream) {

            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            try {
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        private ArrayList<NewsModel> parseResult(String response) {

            ArrayList<NewsModel> list = new ArrayList<NewsModel>();
            try {
                JSONObject jObject= new JSONObject(response);
                JSONArray jArray= jObject.optJSONArray("results");
                for(int i =0;i<jArray.length();i++)
                {
                    JSONObject article = jArray.optJSONObject(i);
                    String title = article.optString("title");
                    String author = article.optString("byline");
                    String imageURL = null;
                    JSONArray mediaArray= article.optJSONArray("media");
                    if(mediaArray!=null){
                        JSONObject mediaObject = mediaArray.optJSONObject(0);
                        JSONArray metadataArray= mediaObject.optJSONArray("media-metadata");
                        imageURL = metadataArray.optJSONObject(0).optString("url");

                    }

                    NewsModel newsArticle = new NewsModel();
                    newsArticle.setTitle(title);
                    newsArticle.setAuthor(author);
                    newsArticle.setImgUrl(imageURL);
                    newsArticle.setUrl(article.optString("url"));

                    list.add(newsArticle);
                }


            } catch (Exception e) {

                e.printStackTrace();
            }
            return list;
        }
    }
}
