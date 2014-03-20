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
		int year = 1980;
		boolean masterpiece = false;
		boolean cult = true;
		String title = "Aterriza como puedas";
		String alternative_title = "¿Y dónde está el piloto?";
		String original_title = "Airplane!";
		String continent = "Améirca";
		String country = "USA";
		String director = "Jim Abrahams, David & Jerry Zucker";
		String genre = "Comedia";
		String poster = "http://ia.media-imdb.com/images/M/MV5BNDU2MjE4MTcwNl5BMl5BanBnXkFtZTgwNDExOTMxMDE@._V1_SX214_.jpg";
		String  filmaffinity_id = "445878";
		String imdb_id = "tt0080339";
		String actor1="Leslie Nielsen";
		String actor2="Robert Hays";
		String actor3="Julie Hagerty";
		String character1="Ted Striker";
		String character2="Dr. Rumack";
		String character3="Elaine";
		String other1 ="El famoso píloto automático que aparece en la película fue bautizado con el nombre de Otto y permaneció años en el garaje del director";
		String other2 ="Posiblemente la película que inició las sagas de parodias basadas en otras películas";
		String other3 ="Todos los actores secundarios fueron escogidos por estar asociados en la mente del público a papeles serios y dramáticos";
		String quote1="No hay ninguna razón para alarmarse, y esperamos que disfruten del vuelo. Por cierto, ¿Hay alguien a bordo que sepa pilotar un avión?";
		String quote2="-Azafata tenemos que llevarlos a un hospital. -¿Qué es, doctor?  -Es un edificio grande lleno de enfermos y casi nunca hay camas.";
		String quote3="-¿Nervioso?. -Sí, un poco. -¿Es la primera vez?. -No, ya había estado nervioso antes.";
		String plot = "El vuelo 209 de la Trans American sale de Los Ángeles con destino a Chicago. Entre el pasaje " +
				"se encuentran una serie de curiosos personajes. Entre ellos, un ex-piloto de combate que, en pleno vuelo, " +
				"se verá obligado a hacerse con el mando del avión comercial, tras quedar los pilotos indispuestos por una " +
				"comida en mal estado.";

		
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
