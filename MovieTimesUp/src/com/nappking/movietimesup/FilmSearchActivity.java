package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.nappking.movietimesup.adapter.MovieListAdapter;
import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class FilmSearchActivity extends DBActivity{
    private EditText inputSearch;
	private ListView movieList;
	private int textLength;
    private String text;
    private List<Movie> movie_sort;
    private List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_list);		

		textLength = 0;
		movie_sort  = new ArrayList<Movie>();
		movies = new ArrayList<Movie>();
		movieList = (ListView) findViewById(android.R.id.list);
		inputSearch = (EditText) findViewById(R.id.inputSearch);
		setListeners();
		update();
    }
	
	@Override 
	protected void onResume() {		
		MovieListAdapter adapter =  (MovieListAdapter) movieList.getAdapter();
		if(adapter!=null)
			adapter.notifyDataSetChanged();
		update();
	    super.onResume();
	}

	private void update() {
		try {
			User user = (User) getHelper().getUserDAO().queryForId(1);
			List<String> idMovies = user.getUnlockedMovies();
			QueryBuilder<Movie, Integer> qb = getHelper().getMovieDAO().queryBuilder();
			Where<Movie,Integer> where = qb.where();
			where.in(Movie.ID, idMovies);
			qb.orderBy(Movie.TITLE, true);
			movies=qb.query();
			MovieListAdapter adapter = new MovieListAdapter(this, R.layout.item_film_searchable, movies);
			movieList.setAdapter(adapter);	
		} catch (SQLException e) {
			e.printStackTrace();
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
					
					//se hace la búsqueda sobre el nombre y el metadato (CIF/NIF o descripcion en caso de grupo)
					if((textLength <=title.length())||(textLength <=original.length())||(textLength <=alternative.length())){
						if(title.contains(text)||original.contains(text)||alternative.contains(text))
							movie_sort.add(movie);
					}
				}
				movieList.setAdapter(new MovieListAdapter(getBaseContext(), R.layout.item_film_searchable, movie_sort));
			}			
		});
		
		movieList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)  {
				Movie movie = (Movie) movieList.getAdapter().getItem(position);
				Intent myIntent = new Intent(getBaseContext(),FilmInfoActivity.class);
				Bundle myBundle = new Bundle();
				myBundle.putSerializable(Movie.class.toString(), movie);
				myIntent.putExtras(myBundle);
				startActivity(myIntent);	
			}	
		});
	}
}
