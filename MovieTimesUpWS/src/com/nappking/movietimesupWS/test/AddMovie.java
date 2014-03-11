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
		int 	year = 2013;
		String  action = SAVE;
		String 	title = "Oblivion";
		String 	alternative_title = "El tiempo del olvido";
		String 	original_title = "Oblivion";
		String 	continent = "Am�rica";
		String 	country = "USA";
		String 	director = "Joseph Kosinski";
		String 	genre = "Sci-Fi";
		String 	poster = "";
		String  filmaffinity_id = "618375";
		String	imdb_id = "tt1483013";
		String 	actor1="Olga Kurylenko";
		String 	actor2="Morgan Freeman";
		String 	actor3="Tom Cruise";
		String 	character1="Julia";
		String 	character2="Malcolm Beech";
		String 	character3="Jack Harper";
		String 	other1 ="Festival de Cine Fant�stico y de Terror de Sitges: Premio Mejor Pel�cula";
		String 	other2 ="Filme de culto en su pa�s -posteriormente se realiz� una secuela y una precuela-, en el a�o 2002 Hollywood realiz� un remake que obtuvo tambi�n un enorme �xito";
		String 	other3 ="Tiene una de las escenas m�s impactantes del cine de terror actual, cuando una ni�a de pelo largo sale de un televisor";
		String 	quote1="-Sabes qu�, mam�? -qu�? -Tomochan vi� el video";
		String 	quote2="Te quedan 7 d�as";
		String 	quote3="Cuatro personas murieron viendo esta cinta";
		String 	plot = "A�o 2073. Hace m�s de 60 a�os la Tierra fue atacada; se gan� la guerra, pero la mitad del planeta " +
				"qued� destruido, y todos los seres humanos fueron evacuados. Un antiguo marine, " +
				"es uno de los �ltimos hombres que la habitan. Es un ingeniero de Drones que participa en una operaci�n " +
				"para extraer los recursos vitales del planeta. Su misi�n consiste en patrullar diariamente los cielos. " +
				"Un d�a, rescata a una desconocida de una nave espacial y, entonces, se ve obligado a replantearse sus " +
				"convicciones m�s profundas.";
		
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
