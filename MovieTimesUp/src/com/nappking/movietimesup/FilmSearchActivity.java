package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.nappking.movietimesup.adapter.CoverAdapter;
import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.widget.CoverFlow;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FilmSearchActivity extends DBActivity{
	private final static String INDEX="INDEX";
	private final static String SEARCH="SEARCH";
    private EditText inputSearch;
    private TextView title;
    private TextView info;
    private ImageView extra;
	private int textLength;
	private int mIndex=0;
	private String mSearch="";
    private String text;
    private List<Movie> movie_sort;
    private List<Movie> movies;
    private CoverFlow coverFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_list);		
		textLength = 0;
		movie_sort  = new ArrayList<Movie>();
		movies = new ArrayList<Movie>();
		coverFlow = (CoverFlow) findViewById(R.id.coverFlow);
		title = (TextView) findViewById(R.id.title);
		info = (TextView) findViewById(R.id.info);
		extra = (ImageView) findViewById(R.id.extra);
        int width = (int) (getResources().getDimension(R.dimen.search_cover_width) /getResources().getDisplayMetrics().density);
    	coverFlow.setSpacing(-width/4);
    	coverFlow.setAnimationDuration(1000);
		inputSearch = (EditText) findViewById(R.id.inputSearch);
		setListeners();
    }
	
	@Override 
	protected void onResume() {		
		CoverAdapter adapter =  (CoverAdapter) coverFlow.getAdapter();
		if(adapter!=null){
			adapter.notifyDataSetChanged();
		}	
		update();
	    super.onResume();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(INDEX, mIndex);
		savedInstanceState.putString(SEARCH, mSearch);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mIndex = savedInstanceState.getInt(INDEX,0);
		mSearch = savedInstanceState.getString(SEARCH, "");
	}
		
	private void update() {
		try {
			if(movies.isEmpty()){
				User user = (User) getHelper().getUserDAO().queryForId(1);
				List<String> idMovies = user.getUnlockedMovies();
				QueryBuilder<Movie, Integer> qb = getHelper().getMovieDAO().queryBuilder();
				Where<Movie,Integer> where = qb.where();
				where.in(Movie.ID, idMovies);
				qb.orderBy(Movie.TITLE, true);
				movies=qb.query();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		if (coverFlow.getAdapter() == null) {
			coverFlow.setAdapter(new CoverAdapter(this, movies));	
		}else{
			((CoverAdapter)coverFlow.getAdapter()).setList(movies);
		}
		inputSearch.setText(mSearch);
		if(mIndex>0){
			coverFlow.setSelection(mIndex, true);
		}
	}
	
	private void setListeners() {
		inputSearch.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s){}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){
				CharSequence searched = inputSearch.getText();
				textLength = searched.length();
				text = ((MovieTimesUpApplication)getApplication()).deAccent(searched.toString().toLowerCase());
				movie_sort.clear();
				for (Movie movie:movies){	
					String title = ((MovieTimesUpApplication)getApplication()).deAccent(movie.getTitle().toLowerCase());
					String original = ((MovieTimesUpApplication)getApplication()).deAccent(movie.getOriginalTitle().toLowerCase());
					String alternative = ((MovieTimesUpApplication)getApplication()).deAccent(movie.getAlternativeTitle().toLowerCase());
					if((textLength <=title.length())||(textLength <=original.length())||(textLength <=alternative.length())){
						if(title.contains(text)||original.contains(text)||alternative.contains(text))
							movie_sort.add(movie);
					}
				}
				coverFlow.setAdapter(new CoverAdapter(getBaseContext(), movie_sort));	
			}			
		});
		
		coverFlow.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)  {
				Movie movie = (Movie) coverFlow.getAdapter().getItem(position);
				Intent myIntent = new Intent(getBaseContext(),FilmInfoActivity.class);
				Bundle myBundle = new Bundle();
				myBundle.putSerializable(Movie.class.toString(), movie);
				myIntent.putExtras(myBundle);
				startActivity(myIntent);	
				mIndex = position;
				mSearch = inputSearch.getText().toString();
			}	
		});
		
		coverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
				Movie movie = (Movie) coverFlow.getAdapter().getItem(position);
				title.setText(movie.getTitle());
				info.setText(movie.getGenre()+" | "+movie.getCountry()+", "+movie.getYear());
				if(movie.getCult()){
					extra.setImageResource(R.drawable.cult_movie);
				}
				else if(movie.getMasterpiece()){
					extra.setImageResource(R.drawable.masterpiece);
				}
				else{
					extra.setImageDrawable(null);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				title.setText("");
				info.setText("");
				extra.setImageDrawable(null);
			}
		});
		
	}
}
