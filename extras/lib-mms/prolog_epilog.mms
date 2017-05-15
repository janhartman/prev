PROLOG: 
ADD $0,$253,0         % stari fp shranim v $0
ADD $253,$254,0        % fp <- sp
SETL $1,40               % $1 <- frame size
SUB $254,$254,$1        % od sp odštejem frame size
SETL $1,16          % old fp offset
SUB $1,$253,$1        % od fp odštejem old fp offset
STO $0,$1,0              % na zgornji naslov shrani stari fp
GET $0,rJ           % dobi povratni naslov
SUB $1,$1,8              % mesto za povratni naslov
STO $0,$1,0              % shrani povratni naslov

EPILOG:
STO $0,$253,0         % Shrani rv na fp
SETL $1,16          % stari fp offset
SUB $1,$253,$1        % odštejem fp-ju
LDO $0,$1,0              % loadam stari fp
SUB $1,$1,8              % return addr
LDO $1,$1,0              % loadam return addr
PUT rJ,$1           % jo nastavim v rJ
ADD $254,$253,0            % sp <- fp
ADD $253,$0,0         % fp <- stari fp
POP 0,0                % return