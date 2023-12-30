import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Helmet } from "react-helmet";
import { useParams } from 'react-router-dom';
import { Requests } from "../requests/Requests";
import { Topic } from "../model/Topic";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { TopicComponent } from "../components/TopicComponent";
import { LoadingComponent } from "../components/LoadingComponent";
import Content from "../components/layout/Content";
import "../style/shared.css"
import "../style/courses.css";
import "../style/topics.css";


export default function CoursePage() {
    const { courseData } = useParams();
    const { token, setToken, secret, setSecret } = useUsosTokens();
    const [topics, setTopics] = useState<Topic[] | null>(null);
    const [loadingTopics, setLoadingTopics] = useState<boolean>(false);
    const [userType, setUserType] = useState<string>("");
    const navigate = useNavigate();

    const addTopicTextStudent = "Zaproponuj własny temat";
    const addTopicTextStaff = "Dodaj temat";

    useEffect(() => {
        setUserType(String(SecurityHelper.getUsetType()));
        if (token && secret) {
            const courseId = courseData?.split("&")[1];
            if (courseId) {
                setLoadingTopics(true);
                Requests.getCourseTopics(token, secret, courseId).then(res => res.res).then(data => {
                    if (data !== undefined) {
                        setTopics(data);
                    }
                })
                .catch(error => {
                    SecurityHelper.clearStorage();
                    navigate("/login");
                })
                .finally(() => {
                    setLoadingTopics(false);
                });
            }
        }

    }, [token, setToken, secret, setSecret]);

    const addTopic = () => {
        navigate("/topic/add/" + courseData);
    };

    const sortTopics = (a: Topic, b: Topic): number => {
        if (!a.temporary && b.temporary)
            return -1;
        if (a.temporary && !b.temporary)
            return 1;
        return a.title.localeCompare(b.title);
    };

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
                        topics != null && topics.sort(sortTopics).map((topic, index) => (
                            <TopicComponent key={index} topic={topic} index={index + 1}/>
                        ))
                    }
                    {
                        loadingTopics &&
                        <LoadingComponent text="Ładowanie tematów" />
                    }
                    {
                        (!loadingTopics && (topics == null || topics.length == 0)) &&
                            <p className="projects-helper-no-topics">
                                Nie ma jeszcze żadnych tematów zdefiniowanych dla tego przedmiotu.
                            </p>
                    }
                    <div className="projects-helper-course-add-topic" onClick={addTopic}>
                        {userType == "STAFF" ? addTopicTextStaff : addTopicTextStudent}
                    </div>
                </div>
            </Content>
        </>
    );
}