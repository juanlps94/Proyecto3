
# Archivo de entrada predeterminado
DEFAULT_INPUT = casoprueba.txt
# Objetivo principal
all: compile

# Objetivo para compilar
compile:
    javac Proyecto_3_LDP.java

# Objetivo para ejecutar
run:
    java Proyecto_3_LDP $(ARGS)