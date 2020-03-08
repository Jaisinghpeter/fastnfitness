package com.easyfitness;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

//import com.google.android.gms.maps.GoogleMap;


public class AboutFragment extends Fragment {
    private String name;
    private int id;
    private MainActivity mActivity = null;

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */

    public static AboutFragment newInstance(String name, int id) {

        AboutFragment f = new AboutFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_about, container, false);
        return view;
    }

    public MainActivity getMainActivity() {
        return this.mActivity;
    }

}

