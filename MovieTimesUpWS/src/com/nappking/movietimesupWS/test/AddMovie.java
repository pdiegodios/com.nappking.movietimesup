package com.nappking.movietimesupWS.test;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;


import com.nappking.movietimesupWS.model.Movie;
import com.nappking.movietimesupWS.util.PSON;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;


public class AddMovie {
	private static String SAVE = "SAVE";
	private static String UPDATE = "UPDATE";
	public static void main(String[] args) {
		
		//VALUES
		int 	id = 19;
		int 	points = 3;
		int 	year = 1998;
		String  action = SAVE;
		String 	title = "The Ring";
		String 	alternative_title = "El círculo";
		String 	original_title = "Ringu";
		String 	continent = "Asia";
		String 	country = "Japón";
		String 	director = "Hideo Nakata";
		String 	genre = "Terror";
		String 	poster = "";
		String  filmaffinity_id = "756516";
		String	imdb_id = "tt1063669";
		String 	actor1="Nanako Matsushima";
		String 	actor2="Miki Nakatani";
		String 	actor3="Hiroyuki Sanada";
		String 	character1="Mai Takano";
		String 	character2="Reiko Asakawa";
		String 	character3="Tomoko Oishi";
		String 	other1 ="Festival de Cine Fantástico y de Terror de Sitges: Premio Mejor Película";
		String 	other2 ="Filme de culto en su país -posteriormente se realizó una secuela y una precuela-, en el año 2002 Hollywood realizó un remake que obtuvo también un enorme éxito";
		String 	other3 ="Tiene una de las escenas más impactantes del cine de terror actual, cuando una niña de pelo largo sale de un televisor";
		String 	quote1="-¿Sabes qué, mamá? -¿qué? -Tomochan vió el video";
		String 	quote2="Te quedan 7 días";
		String 	quote3="Cuatro personas murieron viendo esta cinta";
		String 	plot = "En una pequeña y apacible localidad japonesa, entre los estudiantes circula una " +
				"leyenda en torno a unos videos malditos cuya visión provoca la muerte. Tras la muerte " +
				"de su sobrina, una periodista investigará el origen de dichos videos.";
		
		//CLIENT
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		
		//JSON
		WebResource service = client.resource(getBaseURI());
		PSON pson = new PSON();
		pson.put(Movie.TITLE, title);
		pson.put(Movie.ALTERNATIVE_TITLE, alternative_title);
		pson.put(Movie.ORIGINAL_TITLE, original_title);
		pson.put(Movie.YEAR, year);
		pson.put(Movie.COUNTRY, country);
		pson.put(Movie.CONTINENT, continent);
		pson.put(Movie.DIRECTOR, director);
		pson.put(Movie.GENRE, genre);
		pson.put(Movie.PLOT, plot);
		pson.put(Movie.CAST, new String[]{actor1,actor2,actor3});
		pson.put(Movie.QUOTES, new String[]{quote1,quote2,quote3});
		pson.put(Movie.OTHERS, new String[]{other1,other2,other3});
		pson.put(Movie.CHARACTERS, new String[]{character1,character2,character3});
		pson.put(Movie.POSTER, poster);
		pson.put(Movie.POINTS, points);
		pson.put(Movie.FILMAFFINITY, filmaffinity_id);
		pson.put(Movie.IMDB, imdb_id);
		if(action.equals(UPDATE))
			pson.put(Movie.ROWID, id);
		
		String jsss = "["+pson.toJSON()+"]";
		System.out.println("JSON sent --> "+jsss);
		Form form = new Form();
	    form.add("movies", jsss);
	    form.add("action", action);

	    //RESPONSE
	    ClientResponse  response = service.path("rest").path("movies").type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, form);
	    System.out.println("Response --> " + response.getEntity(String.class));
	
	}

	private static URI getBaseURI() {
		//return UriBuilder.fromUri("http://localhost:8081/MovieTimesUpWS").build();
		return UriBuilder.fromUri("http://movietimesup.gestores.cloudbees.net").build();
	}


}
