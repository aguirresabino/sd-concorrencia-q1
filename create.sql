CREATE TABLE Entidade(
    id serial primary key,
    editado boolean,
    excluido boolean
);

CREATE TABLE Instancia(
    id serial primary key,
    minId int,
    maxId int
);