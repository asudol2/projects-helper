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
