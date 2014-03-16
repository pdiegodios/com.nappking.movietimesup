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
		int year = 1976;
		String  action = SAVE;
		String title = "Taxi Driver";
		String alternative_title = "Taxista";
		String original_title = "Taxi Driver";
		String continent = "América";
		String country = "USA";
		String director = "Martin Scorsese";
		String genre = "Drama";
		String poster = "";
		String  filmaffinity_id = "396074";
		String imdb_id = "tt0075314";
		String actor1="Cybill Shepherd";
		String actor2="Robert de Niro";
		String actor3="Jodie Foster";
		String character1="Travis Bickle";
		String character2="Batsy";
		String character3="Iris";
		String other1 ="4 nominaciones al Oscar incluyendo Mejro Película y Actor";
		String other2 ="Crítica El País: el protagonista se mira al espejo y de un golpe de rabia... queda inaugurado el thriller llamado moderno";
		String other3 ="La hermana mayor de la actriz de 14 años fue su doble para escenas en las que había contenido sexual";
		String quote1="Por la noche, salen todos los animales: putas, sodomitas, travestidos, maricones, toxicómanos... Todo es asqueroso y venal. " +
				"Algún día, una lluvia de verdad se llevará toda esta basura de las calles";
		String quote2="¿Hablas conmigo?";
		String quote3="-No puedo dormir por las noches. -Para eso están los cines porno. -Sí, lo sé. Eso ya lo intenté.";
		String plot = "Para sobrellevar el insomnio crónico que sufre desde su regreso de Vietnam, X trabaja como taxista nocturno en Nueva York. " +
				"Es un hombre insociable que apenas tiene contacto con los demás, se pasa los días en el cine y vive prendado de Y, una atractiva " +
				"rubia que trabaja como voluntaria en una campaña política. Pero lo que realmente obsesiona a X es comprobar cómo la violencia, " +
				"la sordidez y la desolación dominan la ciudad. Y un día decide pasar a la acción.";

		
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
