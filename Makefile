TARGET = Proyecto_3_LDP
JAVAC = javac
SOURCES = Proyecto_3_LDP.java

OBJECTS=$(SOURCES:.java=.class)

$(TARGET):  $(OBJECTS)
    $(JAVAC) $(JFLAGS) $(OBJECTS) -o $(TARGET)

%.class:    %.java $(JAVAC) $(JFLAGS) $< -c

clean:  rm -f $(OBJECTS)

default:    $(TARGET)