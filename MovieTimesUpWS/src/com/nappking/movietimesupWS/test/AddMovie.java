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
		int cinema = 5;
		int year = 1998;
		boolean masterpiece = false;
		boolean cult = false;
		String title = "Salvar al soldado Ryan";
		String alternative_title = "Rescatando al soldado Ryan";
		String original_title = "Saving Private Ryan";
		String continent = "AM";
		String country = "Estados Unidos";
		String director = "Steven Spielberg";
		String genre = "Bélico";
		String poster = "http://ia.media-imdb.com/images/M/MV5BNjczODkxNTAxN15BMl5BanBnXkFtZTcwMTcwNjUxMw@@._V1_SY317_CR9,0,214,317_AL_.jpg";
		String  filmaffinity_id = "353018";
		String imdb_id = "tt0120815";
		String stream = "";
		String actor1="Tom Hanks";
		String actor2="Tom Sizemore";
		String actor3="Matt Damon";
		String character1="Capitán John Miller";
		String character2="Sargento Horvath";
		String character3="Soldado James Ryan";
		String other1 ="Basada en hechos reales: En 1943, los cuatro hermanos Niland, fueron reclutados para luchar en Europa. Tres murieron.";
		String other2 ="Se utilizaron lisiados auténticos para las escenas de gente con miembros amputados.";
		String other3 ="Ese año compitió en los Oscars con otra película del mismo género: \"La delgada línea roja\"";
		String quote1="Cada centímetro de esta playa es un objetivo. Si os quedais aquí, es para morir.";
		String quote2="Si Dios está con nosotros... ¿Quién está con ellos?";
		String quote3="Espero que ese X valga la pena y que cuando regrese a casa cure alguna enfermedad o invente una nueva bombilla de larga duración.";
		String plot="Segunda Guerra Mundial (1939-1945). Tras el desembarco de los Aliados en Normandía, a un grupo de soldados americanos " +
				"se le encomienda una peligrosa misión: poner a salvo al soldado X. Los hombres de la patrulla del capitán Y deben arriesgar " +
				"sus vidas para encontrar a este soldado, cuyos tres hermanos han muerto en la guerra. Lo único que se sabe del soldado X es " +
				"que se lanzó con su escuadrón de paracaidistas detrás de las líneas enemigas. ";

		
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
		//pson.put(Movie.STREAM, stream);
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
