SET client_encoding = 'UTF8';

CREATE ROLE projectshelper WITH LOGIN PASSWORD 'projectshelper';


CREATE TABLE public.oauth_tokens (
    id integer NOT NULL,
    logintoken character varying(128) NOT NULL,
    oauth1_token character varying(64),
    oauth1_secret character varying(64)
);


ALTER TABLE public.oauth_tokens OWNER TO projectshelper;


CREATE SEQUENCE public.oauth_tokens_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.oauth_tokens_id_seq OWNER TO projectshelper;
ALTER SEQUENCE public.oauth_tokens_id_seq OWNED BY public.oauth_tokens.id;
ALTER TABLE ONLY public.oauth_tokens ALTER COLUMN id SET DEFAULT nextval('public.oauth_tokens_id_seq'::regclass);

ALTER TABLE ONLY public.oauth_tokens
    ADD CONSTRAINT oauth_tokens_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.oauth_tokens
    ADD CONSTRAINT unique_logintoken UNIQUE (logintoken);


CREATE TABLE public.topics (
    id integer not null,
    course_id varchar(64) not null,
    lecturer_id integer not null,
    title varchar(64) not null,
    description varchar(512),
    min_team_cap integer not null default 2,
    max_team_cap integer not null default 2,
    temporary bool default False
);

ALTER TABLE public.topics OWNER TO projectshelper;

CREATE SEQUENCE public.topics_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.topics_id_seq OWNER TO projectshelper;
ALTER SEQUENCE public.topics_id_seq OWNED BY public.topics.id;
ALTER TABLE ONLY public.topics ALTER COLUMN id SET DEFAULT nextval('public.topics_id_seq'::regclass);

ALTER TABLE ONLY public.topics
    ADD CONSTRAINT topics_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.topics
    ADD CONSTRAINT unique_title UNIQUE (title);

insert into public.topics(id, course_id, lecturer_id, title, description)
values (1, '103A-INxxx-ISP-ZPR', '38996', 'project_1', 'description_1');

insert into public.topics(id, course_id, lecturer_id, title, description)
values (2, '103A-INxxx-ISP-ZPR', '38996', 'project_2', 'description_2');