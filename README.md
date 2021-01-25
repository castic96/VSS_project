# Hospital Simulator
Semestrální práce z předmětu KIV/VSS.

Pro sestavení a spuštění aplikace je v kořenovém adresáři projektu vytvořen skript `run.bat`, který automatizuje kroky sestavení aplikace a spuštění aplikace, které jsou popsány níže.

## Sestavení aplikace
Pro sestavení aplikace je nutné mít nainstalovány nástroje:
* JDK 8,
* Apache Maven 3.6.2 (nebo vyšší).

Sestavení aplikace se provede příkazem: `mvn clean install` v kořenovém adresáři aplikace. Poté se vytvoří JAR soubor.

## Spuštění aplikace
Pro spuštění aplikace je nutné mít nainstalovaný nástroj JRE 8 (pokud již není nainstalován JDK 8).

Program lze spustit z příkazového řádku následovně: `java -jar VSS_project-1.0-jar-with-dependencies.jar`
