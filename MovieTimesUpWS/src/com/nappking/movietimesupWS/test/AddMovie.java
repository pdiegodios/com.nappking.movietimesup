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
		int 	points = 2;
		int 	year = 1977;
		String  action = SAVE;
		String 	title = "Annie Hall";
		String 	alternative_title = "Dos Extra�os Amantes";
		String 	original_title = "Annie Hall";
		String 	continent = "Am�rica";
		String 	country = "USA";
		String 	director = "Woody Allen";
		String 	genre = "Comedia-Romance";
		String 	poster = "";
		String  filmaffinity_id = "487991";
		String	imdb_id = "tt0075686";
		String 	actor1="Tony Roberts";
		String 	actor2="Woody Allen";
		String 	actor3="Diane Keaton";
		String 	character1="Alvy Singer";
		String 	character2="Rob";
		String 	character3="Annie";
		String 	other1 ="4 Oscars: Pel�cula, director, gui�n original, actriz.";
		String 	other2 ="Una de las mejores pel�culas de su director, la de mayor �xito en USA y, sin duda, un claro reflejo de su propia vida sentimental";
		String 	other3 ="El apellido real de la actriz principal es el de su personaje... Y su apodo es 'Annie'";
		String 	quote1="No te metas con la masturbaci�n, es sexo con alguien a quien amo";
		String 	quote2="Uno siempre est� intentando que las cosas salgan perfectas en el arte, porque conseguirlo en la vida es realmente dif�cil.";
		String 	quote3="...eso es m�s o menos lo que pienso sobre las relaciones humanas, �sabe? son totalmente irracionales, locas y absurdas; pero supongo que continuamos a mantenerlas porque la mayor�a necesitamos los huevos";
		String 	plot = "X, un tipo algo neur�tico, trabaja como humorista en clubs nocturnos. A sus 40 a�os, " +
				"tras romper con Y, su �ltima novia, reflexiona sobre su vida, rememorando sus amores, sus matrimonios, " +
				"pero muy en especial su relaci�n con Y, a la que conoci� en una cancha de tenis. Al final, llega a la " +
				"conclusi�n de que son sus man�as y obsesiones las que siempre acaban arruinando su relaci�n con las mujeres.";
		
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
