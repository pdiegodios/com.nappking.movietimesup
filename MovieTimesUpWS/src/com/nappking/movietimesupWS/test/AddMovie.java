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
		int id = 18;
		int points = 3;
		int year = 2012;
		String  action = SAVE;
		String title = "El vuelo";
		String alternative_title = "the flight";
		String original_title = "Flight";
		String continent = "Am�rica";
		String country = "USA";
		String director = "Robert Zemeckis";
		String genre = "Drama";
		String poster = "";
		String  filmaffinity_id = "526099";
		String imdb_id = "tt1907668";
		String actor1="Don Cheadle";
		String actor2="Kelly Reilly";
		String actor3="Denzel Washington";
		String character1="Hugh Lang";
		String character2="Whip Whitaker";
		String character3="Katerina Marquez";
		String other1 ="La soberbia actuaci�n del veterano actor le supuso un Oscar a mejor actor secundario";
		String other2 ="El protagonista escribi� antes de morir que hab�a vivido intensamente y que hab�a realizado grandes obras. En realidad hab�a sido abandonado y era un alcoh�lico.";
		String other3 ="Edward D.W. Jr. es considerado el peor director de la historia";
		String quote1="Vale la pena luchar por los propios sue�os, �Por qu� pasarse la vida realizando los sue�os de otros?";
		String quote2="�La peor pel�cula que ha visto? Bueno, la pr�xima ser� mejor.";
		String quote3="Viste como aquel ni�o le agarr� las tetas a Vampira?";
		String plot = "Tras un aterrizaje de emergencia en medio del campo gracias al cual salvan la vida un centenar " +
				"de pasajeros, el comandante X, que pilotaba el avi�n, es considerado un h�roe nacional. Sin embargo, " +
				"cuando se pone en marcha la investigaci�n para determinar las causas de la aver�a, se averigua que el " +
				"capit�n ten�a exceso de alcohol en la sangre y que puede ir a la c�rcel si se demuestra que pilot� el " +
				"avi�n en estado de embriaguez.";

		
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
		return UriBuilder.fromUri("http://movietimesup.gestores.cloudbees.net").build();
	}


}
