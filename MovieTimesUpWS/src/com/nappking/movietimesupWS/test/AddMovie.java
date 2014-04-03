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
		String action = SAVE;
		
		int id = 51;
		int points = 2;
		int cinema = 3;
		int year = 1993;
		boolean masterpiece = false;
		boolean cult = true;
		String title = "Pesadilla antes de navidad";
		String alternative_title = "El extraño mundo de Jack";
		String original_title = "The Nightmare Before Christmas";
		String continent = "AM";
		String country = "Estados Unidos";
		String director = "Henry Selick";
		String genre = "Animación";
		String poster = "http://ia.media-imdb.com/images/M/MV5BMTc5MDY0MjkwNV5BMl5BanBnXkFtZTcwNTk2Njk3OA@@._V1_SY317_CR12,0,214,317_.jpg";
		String  filmaffinity_id = "366417";
		String imdb_id = "tt0107688";
		String actor1="Danny Elfman (voz original)";
		String actor2="Oogie Boogie/Oingo Boingo (villano)";
		String actor3="Zero (perro fantasma)";
		String character1="Jack Skellington";
		String character2="Sally";
		String character3="Santa";
		String other1 ="La historia es de Tim Burton";
		String other2 ="El merchandising de la película sigue siendo muy popular, principalmente entre los góticos";
		String other3 ="La banda sonora es de Danny Elfman y es una parte fundamental del film";
		String quote1="¡Esto es Halloween! ¡Esto es Halloween!";
		String quote2="¿Dulce o travesura? Maten al vecino, de ansiedad.";
		String quote3="Los niños se arrojan nieve en vez de tirar cabezas. Están haciendo juguetes y no hay muertos. No hay ningún monstruo, ni siquiera una pesadilla y en su lugar hay sentimientos de alegría.";
		String plot = "Cuando X, descubre la Navidad, se queda fascinado y decide mejorarla. Sin embargo, su visión de la festividad " +
				"es totalmente contraria al espíritu navideño. Sus planes incluyen el secuestro de Santa Claus y la introducción de " +
				"cambios bastante macabros. Sólo su novia Y es consciente del error que está cometiendo. ";

		
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
		pson.put(Movie.CINEMA, cinema);
		pson.put(Movie.MASTERPIECE, masterpiece);
		pson.put(Movie.CULT, cult);
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
	    System.out.println("Response --> " + response.getStatus());
	
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://movietimesup.gestores.cloudbees.net").build();
	}


}
