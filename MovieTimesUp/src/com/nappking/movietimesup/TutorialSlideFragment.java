package com.nappking.movietimesup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TutorialSlideFragment extends Fragment{

    public static final String ARG_PAGE = "page";
    private int mPageNumber;

    public static TutorialSlideFragment create(int pageNumber) {
    	TutorialSlideFragment fragment = new TutorialSlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }
    
    public TutorialSlideFragment(){    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial_slide, container, false);

		switch(mPageNumber){
		case 1: break;
		case 2: break;
		case 3: break;
		case 4: break;
		case 5: break;
		default: break;
		}
		
        return rootView;
	}

    public int getPageNumber() {
        return mPageNumber;
    }
}
