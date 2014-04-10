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
		
		int id = 92;
		int points = 1;
		int cinema = 4;
		int year = 1995;
		boolean masterpiece = false;
		boolean cult = false;
		String title = "Seven";
		String alternative_title = "Pecados capitales";
		String original_title = "Se7en";
		String continent = "AM";
		String country = "Estados Unidos";
		String director = "David Fincher";
		String genre = "Thriller";
		String poster = "http://ia.media-imdb.com/images/M/MV5BMTQwNTU3MTE4NF5BMl5BanBnXkFtZTcwOTgxNDM2Mg@@._V1_SX214_.jpg";
		String  filmaffinity_id = "575149";
		String imdb_id = "tt0114369";
		String actor1="Kevin Spacey";
		String actor2="Brad Pitt";
		String actor3="Morgan Freeman";
		String character1="Somerset";
		String character2="David Mills";
		String character3="John Doe";
		String other1 ="Durante toda la pel�cula el n�mero 7 est� muy presente";
		String other2 ="El presupuesto de la pel�cula era tan corto que la escena final iba a rodarse sin el helic�ptero";
		String other3 ="Spoiler: El motivo por el que el asesino no dejaba huellas en la escena del crimen era porque se las hab�a lijado";
		String quote1="Hemingway escribi�: \"El mundo es un buen lugar por el que vale la pena luchar\"... S�lo estoy de acuerdo con la segunda parte.";
		String quote2="Que el cabr�n tenga un carn� de biblioteca no significa que sea Yoda";
		String quote3="La venganza es necesaria, que estalle... la ira";
		String plot="El veterano teniente X, del departamento de homicidios, est� a punto de jubilarse y ser reemplazado " +
				"por el ambicioso e impulsivo detective Y. Ambos tendr�n que colaborar en la resoluci�n de una serie de " +
				"asesinatos cometidos por un psic�pata que toma como base la relaci�n de los siete pecados capitales: " +
				"gula, pereza, soberbia, avaricia, envidia, lujuria e ira. Los cuerpos de las v�ctimas, sobre los que el " +
				"asesino se ensa�a de manera imp�dica, se convertir�n para los polic�as en un enigma que les obligar� a viajar " +
				"al horror y la barbarie m�s absoluta";

		
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
