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
		int cinema = 3;
		int year = 1994;
		boolean masterpiece = false;
		boolean cult = true;
		String title = "Dos tontos muy tontos";
		String alternative_title = "Una pareja de idiotas";
		String original_title = "Dumb and Dumber";
		String continent = "AM";
		String country = "Estados Unidos";
		String director = "Peter Farrelly";
		String genre = "Comedia";
		String poster = "http://ia.media-imdb.com/images/M/MV5BMzA5MTE0NTUwOV5BMl5BanBnXkFtZTgwOTgyMDUxMDE@._V1_SY317_CR0,0,214,317_.jpg";
		String  filmaffinity_id = "688273";
		String imdb_id = "tt0109686";
		String actor1="Lauren Holly";
		String actor2="Jeff Daniels";
		String actor3="Jim Carrey";
		String character1="Lloyd";
		String character2="Harry";
		String character3="Mary";
		String other1 ="Crítica El País:\"Insufrible y casposa comedia que desaprovecha todo su metraje en unas gracias de lo más chabacano. Impresentable\"";
		String other2 ="En principio al actor principal le ofrecieron 700,000$; pero coincidiendo con el estreno de Ace Ventura, renegoció hasta los $7 millones";
		String other3 ="El actor se retiró la funda que cubre sus dientes astillados años atrás para dar una apariencia más estúpida";
		String quote1="Perdonen a mi amigo, es un poco lento... ¡La ciudad está por ese camino!";
		String quote2="según el mapa llevamos solo 10 centímetros, espero que tengamos bastante dinero para gasolina";
		String quote3="¡No tenemos dinero para comer! ¡No tenemos dinero para llegar a Aspen! ¡No tenemos dinero para dormir!";
		String plot="La vida de X y Z, dos amigos de una estupidez supina, es un auténtico desastre. El primero trabaja como chófer " +
				"de una limousina, y el segundo se dedica a transportar perros. Cuando X se enamora de una chica de buena posición, " +
				"que deja olvidado un maletín en el coche, los dos amigos emprenden un viaje por todo el país para devolvérselo.";

		
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
