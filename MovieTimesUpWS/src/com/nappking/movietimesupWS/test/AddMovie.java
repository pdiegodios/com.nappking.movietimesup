package com.nappking.movietimesupWS.test;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import sun.misc.BASE64Encoder;


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
		int 	id = 14;
		int 	year = 2013;
		int 	points = 1;
		String  action = SAVE;
		String 	title = "Gravity";
		String 	alternative_title = "Gravity";
		String 	original_title = "Gravity";
		String 	country = "USA";
		String 	continent = "America";
		String 	director = "Alfonso Cuarón";
		String 	genre = "Sci-Fi";
		String 	poster = "http://ia.media-imdb.com/images/M/MV5BMjA2Mzc0NTU2N15BMl5BanBnXkFtZTcwOTUwMjUyMQ@@._V1_SY317_CR5,0,214,317_.jpg";
		String 	actor1="Phaldut Sharma";
		String 	actor2="George Clooney";
		String 	actor3="Sandra Bullock";
		String 	character1="Ryan Stone";
		String 	character2="Matt Kowalski";
		String 	character3="Shariff";
		String 	quote1 ="-¿Quieres saber una buena noticia? -¿Qué? -Voy a romper el récord de Anatoly";
		String 	quote2 ="Deberías ver el sol brillando sobre el Ganges, es increíble";
		String 	quote3 ="-¿Qué es lo que más te gusta del espacio? -El silencio...podría acostumbrarme a él";
		String 	other1="Globos de Oro: Mejor director.";
		String 	other2="La película fue mundialmente aclamada por los aspectos técnicos";
		String 	other3="Película de inauguración en el Festival de Venecia.";
		String 	plot = "Mientras reparan un satélite fuera de su nave, dos astronautas " +
				"sufren un grave accidente y quedan flotando en el espacio. Son la doctora " +
				"X, una brillante ingeniera que realiza su primera misión espacial, y el veterano " +
				"astronauta Y. La misión exterior parecía rutinaria, pero una lluvia de basura " +
				"espacial les alcanza y se produce el desastre: el satélite y parte de la nave " +
				"quedan destrozados, dejando a X y Y completamente solos, momento a partir del cual " +
				"intentarán por todos los medios buscar una solución para volver a la Tierra. ";
		
		//CLIENT
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		
		String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            URL url = new URL(poster);
            BufferedImage image = ImageIO.read(url);
            ImageIO.write(image, "jpeg", bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
		
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
