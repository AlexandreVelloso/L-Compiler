/* Exemplo de Programa na linguagem L */

int i,j;
char word[255];
int isPalindrome;

/* Programa Principal */

    /* Le quantidade de caracteres da palavra */
    writeln("Word length:");
    readln(i);

    /* Le palavra */
    writeln("word:");
    readln(word);

    isPalindrome <- 1;

    for j <- 0 to ( (i - 1) / 2 ) - 1 do
    begin
        if( word[j] <> word[i - j - 1] ) then
        begin
                j <- i + 1;
                isPalindrome <- 0;
        end
    end

    if( isPalindrome = 1 ) then
        write(word," is a palindrome");
    else
        write(word," is not a palindrome");