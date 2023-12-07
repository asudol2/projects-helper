import { Helmet } from "react-helmet";
import Content from "../components/layout/Content";
import { Requests } from "../requests/Requests";
import { useEffect, useState } from "react";
import { Topic } from "../model/Topic";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { useNavigate } from "react-router-dom";
import { useParams } from 'react-router-dom';
import { TopicComponent } from "../components/TopicComponent";
import "../style/shared.css"
import "../style/courses.css";
import "../style/topics.css";


export default function CoursePage() {
    const { courseData } = useParams();
    const { token, setToken, secret, setSecret } = useUsosTokens();
    const [topics, setTopics] = useState<Topic[] | null>(null);
    const navigate = useNavigate();


    useEffect(() => {
        if (token && secret) {
            const courseId = courseData?.split("&")[1];
            if (courseId) {
                Requests.getCourseTopics(token, secret, courseId).then(res => res.res).then(data => {
                    if (data !== undefined) {
                        setTopics(data);
                    }
                })
                .catch(error => {
                    SecurityHelper.clearStorage();
                    navigate("/login");
                });
            }
        }

    }, [token, setToken, secret, setSecret]);


    return (
        <>
            <Helmet>
                <title>Zapisy na projekty ∙ przedmiot {courseData?.split("&")[0]}</title>
            </Helmet>
            <Content>
                <div className="App container-fluid projects-helper-course-details">
                    <div className="projects-helper-page-header">{courseData?.split("&")[0]}</div>
                    <div className="projects-helper-page-header-small">Lista dostępnych tematów projektów</div>
                    {
                        topics != null && topics.map((topic, index) => (
                            <TopicComponent key={index} topic={topic} index={index + 1}/>
                        ))
                    }
                    {
                        (topics == null || topics.length == 0) &&
                            <p className="projects-helper-no-topics">
                                Nie ma jeszcze żadnych tematów zdefiniowanych dla tego przedmiotu.
                            </p>
                    }
                </div>
            </Content>
        </>
    );
}