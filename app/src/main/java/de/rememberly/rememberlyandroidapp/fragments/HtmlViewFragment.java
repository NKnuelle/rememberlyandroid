package de.rememberly.rememberlyandroidapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kobjects.nativehtml.android.HtmlView;

import de.rememberly.rememberlyandroidapp.R;

/**
 * Created by nilsk on 02.12.2018.
 */

public class HtmlViewFragment extends Fragment {
    HtmlView htmlView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.htmlview_fragment, container, false);
    }

    @Override
    public void onActivityCreated ( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        htmlView = getView().findViewById(R.id.htmlview);
    }
    public HtmlView getHtmlView() {
        return htmlView;
    }
}
