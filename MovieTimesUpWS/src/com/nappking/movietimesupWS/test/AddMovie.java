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
		int cinema = 1;
		int year = 1986;
		boolean masterpiece = false;
		boolean cult = false;
		String title = "Parque Jur�sico";
		String alternative_title = "Jurassic Park";
		String original_title = "Jurassic Park";
		String continent = "Am�rica";
		String country = "USA";
		String director = "Steven Spielberg";
		String genre = "Ciencia ficci�n";
		String poster = "";
		String  filmaffinity_id = "152490";
		String imdb_id = "tt0107290";
		String actor1="Laura Dern";
		String actor2="Jeff Goldblum";
		String actor3="Sam Neill";
		String character1="Dr. Alan Grant";
		String character2="Dra. Ellie Sattler";
		String character3="Dr. Ian Malcolm";
		String other1 ="Inici� una fiebre de merchandising a su alrededor. Sigue siendo una de las pel�culas m�s taquilleras de todos los tiempos";
		String other2 ="El director afirm� que no permitir�a ver a sus hijos la pel�cula hasta que estos no cumpliesen al menos quince a�os por considerarla demasiado \"terror�fica\"";
		String other3 ="El rugido era una combinaci�n de sonidos de perro, ping�ino, tigre, cocodrilo y elefante";
		String quote1="Yo invito a cient�ficos y usted a una estrella de rock.";
		String quote2="�Lo ha logrado! �Ese loco hijo de puta lo ha logrado!";
		String quote3="-Todos los parques sufren retrasos. Cuando inauguraron Disneylandia en 1956, nada funcion�. -Pero, los piratas del caribe se aver�an, no se comen a los turistas.";
		String plot = "El multimillonario X consigue hacer realidad su sue�o de crear con ellos un parque tem�tico en una isla remota con " +
				"clonaciones de dinosaurios del Jur�sico. Antes de abrirlo al p�blico, invita a una pareja de eminentes cient�ficos y a un " +
				"matem�tico para que comprueben la viabilidad del proyecto. Pero las medidas de seguridad del parque no prev�n el instinto de " +
				"supervivencia de la madre naturaleza ni la codicia humana.";

		
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
