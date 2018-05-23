/* Exemplo de Programa na linguagem L */

/* Area de declaracoes */
int i,j;
final SIZE = 3;
int vector[3];
int temp;
char order[4];

/* Programa Principal */

writeln(" *** Bubble Sort *** ");
writeln("");

/* Le os valores para ordenacao */
for i <- 0 to SIZE - 1 do
begin  
	write("value ", i," : ");
	readln(vector[i]);
end

/* Menu de opcoes */
writeln("");
writeln("Choose sort order: ");
writeln(" asc - ascending");
writeln(" des - descending");
writeln("");
write("option: ");
readln(order);

writeln("");
writeln(" Sorting...");
writeln("");

/* Ordena o vetor na ordem escolhida pelo usuario */

if( order = "asc" ) then
    for i <- 0 to (SIZE - 1) do
    begin
        for j <- 0 to (SIZE - 1) do
        begin
            if( vector[i] < vector[j] ) then
            begin
                temp <- vector[i];
                vector[i] <- vector[j];
                vector[j] <- temp;
            end
        end
    end
else
    for i <- 0 to (SIZE - 1) do
        begin
            for j <- 0 to (SIZE - 1) do
            begin
                if( vector[i] > vector[j] ) then
                begin
                    temp <- vector[i];
                    vector[i] <- vector[j];
                    vector[j] <- temp;
                end
            end
        end

/* exibe vetor ordenado */
for i <- 0 to SIZE - 1 do
	write(vector[i], " ");