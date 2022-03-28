klient1:
	gcc -std=c99 -pedantic -Wall -o klient1.x klient1.c -lm
	./klient1.x

klient2:
	gcc -std=c99 -pedantic -Wall -o klient2.x klient2.c -lm
	./klient2.x

klient3:
	gcc -std=c99 -pedantic -Wall -o klient3.x klient3.c -lm
	./klient3.x
	
DIR = `basename $(CURDIR)`

.PHONY: clean tar
clean:                                                     
	rm -f *.o  *~ *.a *.so *.x core core* a.out; rm -rf ${LIB_DIR}
# Archiwizacja i kompresja
tar: clean
	(cd ../; tar -cvzf $(DIR).tar.gz  $(DIR) )

.SILENT: