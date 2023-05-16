package sii.ms_corrector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import sii.ms_corrector.services.MateriaService;

@SpringBootApplication
public class Main {
  public Main(MateriaService matService) {
    matService.inicializar();
  }

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }
}
