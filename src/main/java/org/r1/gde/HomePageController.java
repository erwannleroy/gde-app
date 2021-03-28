package org.r1.gde;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomePageController {

	@RequestMapping("/")
	  public String landingPage(){
	    return "index.html"; // retorune la page index de Angular
	  }
}
