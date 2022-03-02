run1:
	gcc -std=c99 -pedantic -Wall -o zad1.x zad1.c
	./zad1.x

run2:
	gcc -std=c99 -pedantic -Wall -o zad2.x zad2.c
	./zad2.x

run3:
	gcc -std=c99 -pedantic -Wall -o zad3 zad3.c
	./zad3.x

run4:
	gcc -std=c99 -pedantic -Wall -o zad4.x zad4.c
	./zad4.x

run6:
	gcc -std=c99 -pedantic -Wall -o zad6.x zad6.c
	./zad6.x in.txt out.txt

run7:
	gcc -std=c99 -pedantic -Wall -o zad7.x zad7.c
	./zad7.x in.txt out.txt


DIR = `basename $(CURDIR)`

.PHONY: clean tar
clean:                                                     
	rm -f *.o  *~ *.a *.so *.x core core* a.out; rm -rf ${LIB_DIR}
# Archiwizacja i kompresja
tar: clean
	(cd ../; tar -cvzf $(DIR).tar.gz  $(DIR) )

.SILENT: