serwer:
	gcc -std=c99 -pedantic -Wall -o serwer.x serwer.c -lm
	./serwer.x

DIR = `basename $(CURDIR)`

.PHONY: clean tar
clean:                                                     
	rm -f *.o  *~ *.a *.so *.x core core* a.out; rm -rf ${LIB_DIR}
# Archiwizacja i kompresja
tar: clean
	(cd ../; tar -cvzf $(DIR).tar.gz  $(DIR) )

.SILENT: