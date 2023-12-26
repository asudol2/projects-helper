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
}

export default function AddTopicPage() {
    const { courseData } = useParams();
    const { token, secret } = useUsosTokens();
    const [state, setState] = useState<AddTopicState>({
        title: "",
        description: ""
    });
    const [courseId, setCourseId] = useState<string>("");
    const [validationError, setValidationError] = useState<string>("");
    const navigate = useNavigate();


    useEffect(() => {
            setCourseId(String(courseData?.split("&")[1]));
    }, []);
        
    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>): void => {
        const { name, value } = e.target;
        setState((prevState) => ({ ...prevState, [name]: value }));
    };
    
    const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        if (token && secret) {
            Requests.addTopic(token, secret, courseId, state.title, state.description)
                .then(res => res.res).then(data => {
                if (data == "SUCCESS") {
                    navigate(-1);
                } else {
                    setValidationError(String(data));
                }
            })
            .catch(error => {
                console.log("nie udało się dodać tematu. "+error);
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
                                    className={`form-control ${validationError == "UNIQUE_TITLE_PER_COURSE_AND_TERM" ? 'title-error' : ''} `}
                                    id="title"
                                    name="title"
                                    value={state.title}
                                    onChange={handleChange}
                                    required
                                />
                                {
                                    validationError == "UNIQUE_TITLE_PER_COURSE_AND_TERM" && 
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
                            <button type="submit" className="btn btn-primary">Dodaj Temat</button>
                        </form>
                    </div>
                </div>
            </Content>
        </>
    );
}