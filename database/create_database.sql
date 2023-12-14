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
    id serial primary key,
    course_id varchar(64) not null,
    term varchar(64) not null,
    title varchar(256) not null,
    lecturer_id integer default null,
    description varchar(8192) default null,
    min_team_cap integer not null default 2,
    max_team_cap integer not null default 2,
    temporary bool default False,
    propounder_id varchar(64) default null
);

ALTER TABLE public.topics OWNER TO projectshelper;
ALTER TABLE public.topics
    ADD CONSTRAINT unique_title_per_course_and_term
        UNIQUE (course_id, term, title);


CREATE TABLE public.teams (
    id serial primary key,
    topic_id integer references topics(id)
);
ALTER TABLE public.teams OWNER TO projectshelper;

CREATE TABLE public.team_requests (
    id serial primary key,
    topic_id integer references topics(id)
);
ALTER TABLE public.team_requests OWNER TO projectshelper;

CREATE TABLE public.users_in_teams (
    id serial primary key,
    team_id integer references public.teams(id),
    team_request_id integer references public.team_requests(id),
    user_id varchar(64)
);
ALTER TABLE public.users_in_teams OWNER TO projectshelper;


------------------------------------------------------------------------

insert into public.topics(course_id, lecturer_id, term, title, description)
values ('103A-INxxx-ISP-ZPR', '38996', '2023L', 'project_1', 'description_1');

insert into public.topics(course_id, lecturer_id, term, title, description)
values ('103A-INxxx-ISP-ZPR', '38996', '2023Z', 'project_2', 'description_2');

insert into public.topics(course_id, lecturer_id, term, title, description, temporary, propounder_id)
values ('103D-INxxx-ISP-FO', '1012113', '2023L', 'project_3', 'description_3', false, null);

insert into public.topics(course_id, lecturer_id, term, title, description, max_team_cap, temporary, propounder_id)
values ('103D-INxxx-ISP-FO', '1012113', '2023Z', 'project_4', 'description_4', 4, false, '1158741');

insert into public.topics(course_id, lecturer_id, term, title, description, temporary, propounder_id)
values ('103D-INxxx-ISP-FO', '1012113', '2023L', 'project_5', 'description_5', true, '1158935');

insert into public.topics(course_id, lecturer_id, term, title, description, max_team_cap, temporary, propounder_id)
values ('103D-INxxx-ISP-FO', '1012113', '2023Z', 'project_6', 'description_6', 4, false, '1158935');

insert into public.topics(course_id, lecturer_id, term, title, description, temporary, propounder_id)
values ('103D-INxxx-ISP-FO', '1012113', '2023L', 'project_7', 'description_7', true, '1158741');

insert into public.topics(course_id, lecturer_id, term, title, description, temporary, propounder_id)
values ('103D-INxxx-ISP-FO', '1012113', '2023Z', 'project_8', 'description_8', true, '1158741');

insert into public.topics(course_id, lecturer_id, term, title, description, temporary, propounder_id)
values ('103D-INxxx-ISP-FO', '1012113', '2023L', 'project_9', 'description_9', true, '1158935');

insert into public.topics(course_id, lecturer_id, term, title, description, max_team_cap, temporary, propounder_id)
values ('103D-INxxx-ISP-FO', '1012113', '2023Z', 'project_10', 'description_10', 4, false, '1158935');


insert into public.team_requests(topic_id) values (4);
insert into public.users_in_teams(team_request_id, user_id) values (1,  '1158935');
insert into public.users_in_teams(team_request_id, user_id) values (1,  '1158741');
insert into public.users_in_teams(team_request_id, user_id) values (1,  '1158940');
insert into public.users_in_teams(team_request_id, user_id) values (1,  '1158948');

insert into public.team_requests(topic_id) values (6);
insert into public.users_in_teams(team_request_id, user_id) values (2,  '1158935');
insert into public.users_in_teams(team_request_id, user_id) values (2,  '1158741');
insert into public.users_in_teams(team_request_id, user_id) values (2,  '1158940');

insert into public.team_requests(topic_id) values (10);
insert into public.users_in_teams(team_request_id, user_id) values (3,  '1158935');
insert into public.users_in_teams(team_request_id, user_id) values (3,  '1158741');

------------------------------------------------------------------------