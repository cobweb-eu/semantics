@echo off

mkdir www\surveys
mkdir www\context

REM for /R Ontology --ns= %%f in (*.ttl) do (
REM     echo %%~nf
REM )

mkdir temp
pushd temp

FOR %%f IN (..\Ontology\*.ttl) DO (
REM    echo www\%%~nf
    echo Converting %%f
    
    call "C:\Storage\Dev\SDK\Python35\Scripts\rdfpipe.exe" -i turtle -o turtle  %%f > ..\www\%%~nf.ttl
    call "C:\Storage\Dev\SDK\Python35\Scripts\rdfpipe.exe" -i turtle -o xml     %%f > ..\www\%%~nf.rdf
    call "C:\Storage\Dev\SDK\Python35\Scripts\rdfpipe.exe" -i turtle -o n3      %%f > ..\www\%%~nf.n3
    call "C:\Storage\Dev\SDK\Python35\Scripts\rdfpipe.exe" -i turtle -o json-ld %%f > ..\www\%%~nf.jsonld
    call "C:\Storage\Dev\Tools\GnuWin32\bin\sed.exe" -i -f ..\json-ld.sed.txt ..\www\%%~nf.jsonld
    
    echo URI: %%~nf                             > ..\www\%%~nf.var
    echo.                                       >> ..\www\%%~nf.var
    echo URI: %%~nf.ttl                         >> ..\www\%%~nf.var        
    echo Content-type: application/x-turtle;    >> ..\www\%%~nf.var
    echo.                                       >> ..\www\%%~nf.var
    echo URI: %%~nf.rdf                         >> ..\www\%%~nf.var
    echo Content-type: application/rdf+xml;     >> ..\www\%%~nf.var
    echo.                                       >> ..\www\%%~nf.var
    echo URI: %%~nf.n3                          >> ..\www\%%~nf.var
    echo Content-type: text/n3;                 >> ..\www\%%~nf.var
    echo.                                       >> ..\www\%%~nf.var
    echo URI: %%~nf.jsonld                      >> ..\www\%%~nf.var
    echo Content-type: application/ld+json;     >> ..\www\%%~nf.var
)

FOR %%f IN (..\Ontology\surveys\*.ttl) DO (
REM    echo www\surveys\%%~nf
    echo Converting %%f
    
    call "C:\Storage\Dev\SDK\Python35\Scripts\rdfpipe.exe" -i turtle -o turtle  %%f > ..\www\surveys\%%~nf.ttl
    call "C:\Storage\Dev\SDK\Python35\Scripts\rdfpipe.exe" -i turtle -o xml     %%f > ..\www\surveys\%%~nf.rdf
    call "C:\Storage\Dev\SDK\Python35\Scripts\rdfpipe.exe" -i turtle -o n3      %%f > ..\www\surveys\%%~nf.n3
    call "C:\Storage\Dev\SDK\Python35\Scripts\rdfpipe.exe" -i turtle -o json-ld %%f > ..\www\surveys\%%~nf.jsonld
    call "C:\Storage\Dev\Tools\GnuWin32\bin\sed.exe" -i -f ..\json-ld.sed.txt ..\www\surveys\%%~nf.jsonld
    
    echo URI: %%~nf                             > ..\www\surveys\%%~nf.var
    echo.                                       >> ..\www\surveys\%%~nf.var
    echo URI: %%~nf.ttl                         >> ..\www\surveys\%%~nf.var        
    echo Content-type: application/x-turtle;    >> ..\www\surveys\%%~nf.var
    echo.                                       >> ..\www\surveys\%%~nf.var
    echo URI: %%~nf.rdf                         >> ..\www\surveys\%%~nf.var
    echo Content-type: application/rdf+xml;     >> ..\www\surveys\%%~nf.var
    echo.                                       >> ..\www\surveys\%%~nf.var
    echo URI: %%~nf.n3                          >> ..\www\surveys\%%~nf.var
    echo Content-type: text/n3;                 >> ..\www\surveys\%%~nf.var
    echo.                                       >> ..\www\surveys\%%~nf.var
    echo URI: %%~nf.jsonld                      >> ..\www\surveys\%%~nf.var
    echo Content-type: application/ld+json;     >> ..\www\surveys\%%~nf.var
)

popd
del /Q temp\*
rmdir temp

copy Ontology\context\* www\context\

REM 

REM pause
REM exit

