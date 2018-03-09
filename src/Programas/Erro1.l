/* Exemplo de Programa na linguagem L */

int n[3], max, i;
final constante = 5;

/* Programa Principal */

  for i<-0 to 2 do begin
    write("Digite um numero: ");
    readln(n[i]);
  end
  if n[0] > n[1] then begin
    if n[0] > n[2] then max<-n[0]; else max<-n[2];
  end else if n[1] > n[2] then max<-n[1]; else max<-n[2];

  if( n <= 5 )then begin
    n <- n<>6;

  writeln("O maior numero e´ ",max);
  
/*