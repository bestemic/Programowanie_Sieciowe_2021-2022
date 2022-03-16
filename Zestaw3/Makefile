run5:
	gcc -std=c99 -pedantic -Wall -o zad5.x zad5.c
	./zad5.x 20123

run7:
	gcc -std=c99 -pedantic -Wall -o zad7.x zad7.c
	./zad7.x 127.0.0.1 20123

run9s:
	gcc -std=c99 -pedantic -Wall -o zad9_serwer.x zad9_serwer.c
	./zad9_serwer.x 20123

run9c:
	gcc -std=c99 -pedantic -Wall -o zad9_klient.x zad9_klient.c
	./zad9_klient.x 127.0.0.1 20123

DIR = `basename $(CURDIR)`

.PHONY: clean tar
clean:                                                     
	rm -f *.o  *~ *.a *.so *.x core core* a.out; rm -rf ${LIB_DIR}
# Archiwizacja i kompresja
tar: clean
	(cd ../; tar -cvzf $(DIR).tar.gz  $(DIR) )

.SILENT: