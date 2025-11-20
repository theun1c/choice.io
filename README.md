# Unloaded service

Сервис для выгрузки данных об аниме из MyAnimeList в Supabase.

## База данных

``` sql 

CREATE TABLE anime (
    id BIGSERIAL PRIMARY KEY,
    mal_id INTEGER UNIQUE NOT NULL,
    url TEXT NOT NULL,
    images JSONB,
    title TEXT NOT NULL,
    title_english TEXT,
    type TEXT,
    episodes INTEGER,
    status TEXT,
    rating TEXT,
    score DOUBLE PRECISION,
    synopsis TEXT,
    year INTEGER
);

CREATE TABLE genres (
    id BIGSERIAL PRIMARY KEY,
    mal_id INTEGER UNIQUE NOT NULL,
    type TEXT,
    name TEXT NOT NULL,
    url TEXT
);

CREATE TABLE anime_genres (
    anime_id BIGINT REFERENCES anime(id) ON DELETE CASCADE,
    genre_id BIGINT REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (anime_id, genre_id)
);

```

## Использование 

создать .env файл в корне проекта

``` bash 
API_KEY=твой_supabase_ключ
API_URL=твой_supabase_адрес

``` 

Запустить сервис и следовать инструкциям в консоли 

``` bash 
unloader := services.NewUnloader()
unloader.Start()
```

## Что делает 

- Загружает аниме с MyAnimeList
- Проверяет дубликаты
- Сохраняет в 3 таблицы: аниме, жанры и связи между ними
- Показывает статистику по каждой странице

## Структура 

- anime - основные данные аниме
- genres - жанры
- anime_genres - связи аниме с жанрами (многие-ко-многим)


## TODO 
- emprove architecture
