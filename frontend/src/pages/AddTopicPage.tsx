import { Helmet } from "react-helmet";
import { useNavigate } from "react-router-dom";
import { useParams } from 'react-router-dom';
import { Requests } from "../requests/Requests";
import { ChangeEvent, FormEvent, useEffect, useState } from "react";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import Content from "../components/layout/Content";
import "../style/shared.css"
import "../style/courses.css";
import "../style/topics.css";


interface AddTopicState {
    title: string;
    description: string;
    minCap: number;
    maxCap: number;
}

export default function AddTopicPage() {
    const { courseData } = useParams();
    const { token, secret } = useUsosTokens();
    const [state, setState] = useState<AddTopicState>({
        title: "",
        description: "",
        minCap: 2,
        maxCap: 2,
    });
    const [courseId, setCourseId] = useState<string>("");
    const [validationError, setValidationError] = useState<string>("");
    const navigate = useNavigate();


    useEffect(() => {
            setCourseId(String(courseData?.split("&")[1]));
    });
        
    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>): void => {
        const { name, value } = e.target;
        if (name === "minCap" || name === "maxCap") {
            const numericValue = value.replace(/[^0-9]/g, '');
            e.target.value = numericValue;
        }
        setState((prevState) => ({ ...prevState, [name]: value }));
    };
    
    const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        if (token && secret) {
            Requests.addTopic(token, secret, courseId, state.title, state.description, state.minCap, state.maxCap)
                .then(res => res.res).then(data => {
                if (data === "SUCCESS") {
                    navigate(-1);
                } else {
                    setValidationError(String(data));
                }
            })
            .catch(error => {
                setValidationError("Wystąpił błąd, spróbuj ponownie za chwilę.");
            });
        }
    };


    return (
        <>
            <Helmet>
                <title>Zapisy na projekty ∙ zaproponuj własny temat projektu</title>
            </Helmet>
            <Content>
                <div className="App container-fluid projects-helper-course-details">
                    <div className="projects-helper-page-header">{courseData?.split("&")[0]}</div>
                    <div className="container">
                        <h2 className="mt-4 mb-4">Zaproponuj nowy temat projektu</h2>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label htmlFor="title">Tytuł:</label>
                                <input 
                                    type="text"
                                    className={`form-control ${validationError === "UNIQUE_TITLE_PER_COURSE_AND_TERM" ? 'title-error' : ''} `}
                                    id="title"
                                    name="title"
                                    value={state.title}
                                    onChange={handleChange}
                                    required
                                />
                                {
                                    validationError === "UNIQUE_TITLE_PER_COURSE_AND_TERM" && 
                                    <span className="title-error">
                                        Temat o tym tytule już istnieje w ramach tego przedmiotu.
                                    </span>
                                }
                            </div>
                            <div className="form-group projects-helper-add-topic-description">
                                <label htmlFor="description">Opis:</label>
                                <textarea
                                    className="form-control"
                                    id="description"
                                    name="description"
                                    value={state.description}
                                    onChange={handleChange}
                                    required
                                ></textarea>
                            </div>
                            <div className="form-container">
                                <div className="form-group projects-helper-add-topic-min-cap">
                                    <label htmlFor="minCap">Minimalna liczebność zespołu:</label>
                                    <input
                                        type="number-input"
                                        name="minCap"
                                        id="minCap"
                                        min="1"
                                        onChange={handleChange}
                                        defaultValue={state.minCap}
                                        className={`${validationError === "MIN_TEAM_CAP" ? 'capacity-error' : ''} `}
                                        required
                                    />
                                </div>

                                <div className="form-group projects-helper-add-topic-max-cap">
                                    <label htmlFor="maxCap">Maksymalna liczebność zespołu:</label>
                                    <input
                                        type="number-input"
                                        name="maxCap"
                                        id="maxCap"
                                        min="1"
                                        onChange={handleChange}
                                        defaultValue={state.maxCap}
                                        className={`${validationError === "MIN_TEAM_CAP" ? 'capacity-error' : ''} `}
                                        required
                                    />
                                </div>
                                {
                                    validationError === "MIN_TEAM_CAP" &&
                                    <span className="capacity-error">
                                        Maksymalna liczebność zespołu nie może być mniejsza od minimalnej.
                                    </span>
                                }
                            </div>

                            <button type="submit" className="btn btn-primary">Dodaj temat</button>
                        </form>
                    </div>
                </div>
            </Content>
        </>
    );
}