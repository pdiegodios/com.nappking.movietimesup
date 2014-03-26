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
		boolean cult = true;
		String title = "Snatch. Cerdos y diamantes";
		String alternative_title = "Cerdos y diamantes";
		String original_title = "Snatch";
		String continent = "EU";
		String country = "UK";
		String director = "Guy Ritchie";
		String genre = "Thriller-Comedia Negra";
		String poster = "http://ia.media-imdb.com/images/M/MV5BMTk5NzE0MDQyNl5BMl5BanBnXkFtZTcwNzk4Mjk3OA@@._V1_SY317_CR2,0,214,317_.jpg";
		String  filmaffinity_id = "568510";
		String imdb_id = "tt0208092";
		String actor1="Brad Pitt";
		String actor2="Benicio Del Toro";
		String actor3="Vinnie Jones";
		String character1="Franky \"Cuatro dedos\"";
		String character2="Tony \"Dientes de bala\"";
		String character3="Turco";
		String other1 ="Uno de los actores fue un afamado jugador de futbol conocido por sus salvajes entradas";
		String other2 ="En la película suena \"Lucky Star\" de Madonna, que ese año se casó con el director";
		String other3 ="Crítica P.Kurt: \"delirante y violento filme con mucho humor negro(...). Fiel a su estilo, el director inglés volvió a no defraudar a sus numerosos seguidores y reafirmar a sus detractores.\"";
		String quote1="¿Para qué cojones quiero una caravana sin una puta rueda?";
		String quote2="Nunca debéis subestimar lo predecible que es la estupidez.";
		String quote3="Para cada acción hay una reacción, y la reacción de un gitano es algo muy jodido.";
		String plot = "X es un ladrón de diamantes que tiene que entregar un valioso ejemplar a su jefe Y, pero, antes de hacerlo, " +
				"se deja convencer por un tal Z para apostar en un combate ilegal de boxeo. En realidad, se trata de una trampa para " +
				"arrebatarle el diamante. Cuando Y se entera, contrata a W para encontrar a X y al diamante. Descubierto el triste " +
				"destino de X, la recuperación de la gema desaparecida provoca una situación caótica, donde el engaño, el chantaje y el " +
				"fraude se mezclan de forma sangrienta con perros, diamantes, boxeadores y gran variedad de armas.";

		
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
