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
		int points = 1;
		int year = 1995;
		String  action = SAVE;
		String title = "Pocahontas";
		String alternative_title = "Pokahontas";
		String original_title = "Pocahontas";
		String continent = "Europa";
		String country = "UK";
		String director = "Mike Gabriel & Eric Goldberg";
		String genre = "Animación";
		String poster = "";
		String  filmaffinity_id = "902616";
		String imdb_id = "tt0114148";
		String actor1="Mel Gibson (voz original)";
		String actor2="Christian Bale (voz original)";
		String actor3="Linda Hunt (voz original)";
		String character1="Gobernador Ratcliffe";
		String character2="Jefe Powhatan";
		String character3="Capitán Smith";
		String other1 ="2 Oscars: Mejor canción original, Mejor banda sonora";
		String other2 ="Supone el primer romance interracial en una película de Disney y los primeros personajes con existencia verídica e histórica";
		String other3 ="Los dibujantes se basaron en la modelo Naomi Campbell para dibujar al personaje principal";
		String quote1="Hemos mejorado las vidas de salvajes de todo el mundo";
		String quote2="Preferiría morir mañana, que vivir cien años sin haberte conocido";
		String quote3="-¡Pero esta es su tierra! -¡Esta es mi tierra!¡Yo hago las leyes aquí!";
		String plot = "X vigila la llegada de un gran grupo de colonos ingleses, guiados por el ambicioso " +
				"gobernador y el valiente capitán Z. Con su juguetón compañero Meeko, un travieso mapache, y con Flit, un alegre pájaro, " +
				"X entabla una fuerte amistad con el Capitán Z. Pero cuando empiezan a surgir tensiones entre las dos culturas, X recurre " +
				"a la sabiduría de la Abuela Sauce para encontrar una manera de lograr la paz entre su pueblo y los conquistadores.";

		
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
