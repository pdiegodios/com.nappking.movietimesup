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
		int points = 3;
		int year = 1994;
		String  action = SAVE;
		String title = "Ed Wood";
		String alternative_title = "Pokahontas";
		String original_title = "Ed Wood";
		String continent = "América";
		String country = "USA";
		String director = "Tim Burton";
		String genre = "Comedia";
		String poster = "";
		String  filmaffinity_id = "655275";
		String imdb_id = "tt0109707";
		String actor1="Patricia Arquette";
		String actor2="Johnny Depp";
		String actor3="Martin Landau";
		String character1="Kathy O'Hara";
		String character2="Bela Lugosi";
		String character3="Ed Wood";
		String other1 ="La soberbia actuación del veterano actor le supuso un Oscar a mejor actor secundario";
		String other2 ="El protagonista escribió antes de morir que había vivido intensamente y que había realizado grandes obras. En realidad había sido abandonado y era un alcohólico.";
		String other3 ="Edward D.W. Jr. es considerado el peor director de la historia";
		String quote1="Vale la pena luchar por los propios sueños, ¿Por qué pasarse la vida realizando los sueños de otros?";
		String quote2="¿La peor película que ha visto? Bueno, la próxima será mejor.";
		String quote3="Viste como aquel niño le agarró las tetas a Vampira?";
		String plot = "X es un joven director de cine, un visionario sin ninguna formación académica, aficionado " +
				"a vestirse de mujer y con muy pocas oportunidades de hacer películas en un gran estudio. " +
				"Sin embargo no ceja en su empeño de convertirse en un director famoso. Tras reunir a un " +
				"curioso grupo de personajes, realiza películas de bajo presupuesto, excéntricas y no muy " +
				"cuidadas técnicamente. ";

		
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
