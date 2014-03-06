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
		int 	id = 19;
		int 	points = 3;
		int 	year = 2004;
		String  action = SAVE;
		String 	title = "La ola";
		String 	alternative_title = "ola";
		String 	original_title = "Die welle";
		String 	continent = "Europa";
		String 	country = "Alemania";
		String 	director = "Dennis Gansel";
		String 	genre = "Drama";
		String 	poster = "";
		String  filmaffinity_id = "695239";
		String	imdb_id = "tt1063669";
		String 	actor1="Jürgen Vogel";
		String 	actor2="Frederick Lau";
		String 	actor3="Jennifer Ulrich";
		String 	character1="Rainer Wenger";
		String 	character2="Tim Stoltefuss";
		String 	character3="Karo";
		String 	other1 ="Inspirado en un experimento llevado a cabo por Ron Jones en el instituto Cubberley de Palo Alto, California";
		String 	other2 ="Crítica ABC:\"Pedagógica, visceral, catártica, agitadora, clarividente...\"";
		String 	other3 ="Fue controvertida en su país de origen por el tema tratado";
		String 	quote1="Me gustaría ser su guardaespaldas";
		String 	quote2="Lo siento, pero creo que se te está yendo de las manos. Completamente";
		String 	quote3="¿Creéis que en Alemania no sería posible que volviera una dictadura, verdad?";
		String 	plot = "En Alemania, durante la semana de proyectos, al profesor de instituto X se le ocurre hacer un experimento " +
				"para explicar a sus alumnos el funcionamiento de un régimen totalitario. En apenas unos días, lo que parecía una " +
				"prueba inócua basada en la disciplina y el sentimiento de comunidad va derivando hacia una situación sobre la que " +
				"el profesor pierde todo control. ";
		
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
		//return UriBuilder.fromUri("http://localhost:8081/MovieTimesUpWS").build();
		return UriBuilder.fromUri("http://movietimesup.gestores.cloudbees.net").build();
	}


}
