# Makefile para compilar y ejecutar el proyecto

# Archivos fuente
SOURCES = Proyecto_3_LDP.java

# Archivos .class
CLASSES = $(SOURCES:.java=.class)

# Compilador de Java
JAVAC = javac

# Intérprete de Java
JAVA = java

# Directorio donde se encuentran los archivos fuente
SRC_DIR = .

# Casos de prueba
DEFAULT_INPUT = casoprueba.txt

#
all: compile

# Compilar los archivos .java
compile: $(CLASSES)

%.class: $(SRC_DIR)/%.java
	@$(JAVAC) $<

# Limpiar los archivos compilados
clean:
	rm -f *.class

# Ejecutar el programa principal con un archivo de entrada
run: compile
	$(JAVA) Principal $(ARGS)

.PHONY: all compile clean run