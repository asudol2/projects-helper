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
    const navigate = useNavigate();


    useEffect(() => {
        if (token && secret) {
            Requests.getAllCourses(token, secret).then(res => res.res).then(data => {
                if (data !== undefined) {
                    setCourses(data);
                }
            })
            .catch(error => {
                SecurityHelper.clearStorage();
                navigate("/login");
            });
        }
    }, [token, setToken, secret, setSecret]);


    return (
        <>
            <Helmet>
                <title>Zapisy na projekty ∙ Strona główna</title>
            </Helmet>
            <Content>
                <div className="App container-fluid projects-helper-courses-cont">
                    <p className="container-fluid projects-helper-page-header">Przedmioty w bieżącej realizacji 23Z</p>
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