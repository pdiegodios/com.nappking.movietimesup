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
		int year = 2012;
		boolean masterpiece = false;
		boolean cult = false;
		String title = "Django desencadenado";
		String alternative_title = "Django";
		String original_title = "Django unchained";
		String continent = "AM";
		String country = "Estados Unidos";
		String director = "Quentin Tarantino";
		String genre = "Western";
		String poster = "http://ia.media-imdb.com/images/M/MV5BMjIyNTQ5NjQ1OV5BMl5BanBnXkFtZTcwODg1MDU4OA@@._V1_SX214_.jpg";
		String  filmaffinity_id = "929558";
		String imdb_id = "tt1853728";
		String actor1="Leonardo DiCaprio";
		String actor2="Christoph Waltz";
		String actor3="Jamie Foxx";
		String character1="Dr. King Schultz";
		String character2="Django";
		String character3="Calvin Candie";
		String other1 ="Un cameo del director en la película termina de manera \"explosiva\"";
		String other2 ="Ennio Morricone dijo del director: \"coloca la música sin coherencia y no puedes hacer nada con alguien así\"";
		String other3 ="La película está llena de guiños a otras películas del género western, principalmente de Sergio Corbucci, de quien el director es fan";
		String quote1="La \"d\" es muda, paleto";
		String quote2="-He contado seis tiros, negro. -He contado dos armas, negro";
		String quote3="-¡Me ha roto la pierna! -Sin duda. Ahora si hace el favor de mantener sus berridos al mínimo podré terminar de interrogar al joven";
		String plot = "En Texas, dos años antes de estallar la Guerra Civil Americana, un cazarecompensas alemán que le sigue la pista a unos asesinos " +
				"para cobrar por sus cabezas, le promete al esclavo negro X dejarlo en libertad si le ayuda a atraparlos. Él acepta pues luego quiere ir " +
				"a buscar a su esposa Y, una esclava que están en una plantación del terrateniente Z";

		
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
