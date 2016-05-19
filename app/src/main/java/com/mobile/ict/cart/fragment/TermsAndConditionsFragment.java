package com.mobile.ict.cart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.ict.cart.R;

/**
 * Created by vish on 21/3/16.
 */
public class TermsAndConditionsFragment extends Fragment {

    View termsAndConditionsFragmentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        termsAndConditionsFragmentView = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);
        getActivity().setTitle(R.string.title_fragment_terms_and_conditions);

        return termsAndConditionsFragmentView;
    }
}
