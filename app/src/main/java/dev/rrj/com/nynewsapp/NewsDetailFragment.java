package dev.rrj.com.nynewsapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;



public class NewsDetailFragment extends Fragment {


    ProgressBar progressBar;
    WebView wView;
    public NewsDetailFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        wView = (WebView)view.findViewById(R.id.webView);
        wView.setWebViewClient(new WebViewClient());
        progressBar = (ProgressBar)view.findViewById(R.id.progressbar);
        wView.setWebChromeClient(new WebChromeClient(){

            public void onProgressChanged(WebView view,int progress){
                progressBar.setProgress(progress);
                if(progress==100)
                    progressBar.setVisibility(View.GONE);
                else
                    progressBar.setVisibility(View.VISIBLE);
            }
        });

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            String url = extras.getString("URL");
            wView.loadUrl(url);
        }
        return view;
    }
    

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
    }

    public void loadLink(String uri) {
        wView.loadUrl(uri);
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

}
