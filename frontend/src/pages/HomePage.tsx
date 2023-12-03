import { Helmet } from "react-helmet";
import { CourseComponent } from "../components/CourseComponent";
import Content from "../components/layout/Content";
import { useEffect, useState } from "react";
import { Requests } from "../requests/Requests";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { Course } from "../model/Course";
import { useNavigate } from "react-router-dom";
import { SecurityHelper } from "../helpers/SecurityHelper";

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
                <div className="App container-fluid projects-helper-main-page">
                    {courses != null &&
                        courses.map((course, index) => (
                            <CourseComponent key={index} name={Object.keys(course.names)[0]} id={course.courseID} />
                        ))}
                </div>
            </Content>
        </>
    );
}