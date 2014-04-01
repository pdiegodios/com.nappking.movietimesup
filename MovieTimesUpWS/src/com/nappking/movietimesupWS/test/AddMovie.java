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
		int points = 1;
		int cinema = 2;
		int year = 2000;
		boolean masterpiece = false;
		boolean cult = false;
		String title = "Gladiator";
		String alternative_title = "Gladiador";
		String original_title = "Gladiator";
		String continent = "AM";
		String country = "Estados Unidos";
		String director = "Ridley Scott";
		String genre = "Acción";
		String poster = "http://ia.media-imdb.com/images/M/MV5BMTgwMzQzNTQ1Ml5BMl5BanBnXkFtZTgwMDY2NTYxMTE@._V1_SY317_CR0,0,214,317_.jpg";
		String  filmaffinity_id = "392075";
		String imdb_id = "tt0172495";
		String actor1="Russell Crowe";
		String actor2="Joaquin Phoenix";
		String actor3="Richard Harris";
		String character1="Máximo";
		String character2="Cómodo";
		String character3="Marco Aurelio";
		String other1 ="5 Oscars: Mejor película, actor y tres técnicos";
		String other2 ="Al principio de la película, cuando está todo el campo de batalla cubierto de cadáveres, uno de esos cadáveres aparece vestido con un pantalón vaquero";
		String other3 ="Crítica Cinemanía: \"Emocionante retorno a un género perdido (...) La aventura está servida, los que la vamos a disfrutar la saludamos\"";
		String quote1="Lo que hacemos en la vida tiene su eco en la eternidad";
		String quote2="Si os veis cabalgando solos por verdes prados, el rostro bañado por el sol, que no os cause temor, estareis en el Elíseo y ya habréis muerto";
		String quote3="...padre de un hijo asesinado, marido de una mujer asesinada, y alcanzaré mi venganza en esta vida o en la otra.";
		String plot = "En el año 180, el Imperio Romano domina todo el mundo conocido. Tras una gran victoria sobre " +
				"los bárbaros del norte, el anciano emperador X decide transferir el poder a Y, bravo general de sus " +
				"ejércitos y hombre de inquebrantable lealtad al imperio. Pero su hijo Z, que aspiraba al trono, no lo " +
				"acepta y trata de asesinar a Y.";

		
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
