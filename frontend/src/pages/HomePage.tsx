import { useEffect, useState } from "react";
import { Helmet } from "react-helmet";
import { useNavigate } from "react-router-dom";
import { CourseComponent } from "../components/CourseComponent";
import { Requests } from "../requests/Requests";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { Course } from "../model/Course";
import { SecurityHelper } from "../helpers/SecurityHelper";
import Content from "../components/layout/Content";
import "../style/shared.css"
import "../style/courses.css";


export default function LoginPage() {
    const [courses, setCourses] = useState<Course[] | null>(null);
    const { token, setToken, secret, setSecret } = useUsosTokens();
    const [headerWithTerm, setHeaderWithTerm] = useState<string>("");
    const navigate = useNavigate();

    const headerTextTemplateStudent = "Przedmioty w bieżącej realizacji ";
    
        const redirectToLoginPage = () => {
            SecurityHelper.clearStorage();
            navigate("/login");
        }

    useEffect(() => {
        if (token && secret) {
            Requests.getAllCourses(token, secret).then(res => res.res).then(data => {
                if (data !== undefined) {
                    setCourses(data);
                } else {
                    redirectToLoginPage();
                }
            })
            .catch(error => {
                redirectToLoginPage();
            });
            const cachedTerm = SecurityHelper.getTerm();
            if (cachedTerm != null) {
                setHeaderWithTerm(headerTextTemplateStudent+cachedTerm);
                return;
            }
            Requests.getCurrentTerm(token, secret).then(res => res.res).then(data => {
                if (data !== undefined) {
                    setHeaderWithTerm(headerTextTemplateStudent+data);
                    SecurityHelper.saveTerm(data);
                } else {
                    redirectToLoginPage();
                }
            })
            .catch(error => {
                redirectToLoginPage();
            });
        }
    }, [token, setToken, secret, setSecret, redirectToLoginPage]);


    return (
        <>
            <Helmet>
                <title>Zapisy na projekty ∙ Strona główna</title>
            </Helmet>
            <Content>
                <div className="App container-fluid projects-helper-courses-cont">
                    <p className="container-fluid projects-helper-page-header">{headerWithTerm}</p>
                    {
                        courses != null &&
                        courses.map((course, index) => (
                            <CourseComponent key={index} name={Object.keys(course.names)[0]} id={course.courseID} />
                        ))
                    }
                </div>
            </Content>
        </>
    );
}