import { useNavigate } from "react-router-dom";

interface CourseComponentProps {
    key: number;
    id: string;
    name: string;
}

export function CourseComponent(props: CourseComponentProps) {
    const navigate = useNavigate();


    const handleClick = () => {
        navigate("/course/"+props.name+"&"+props.id);
    }
    return (
        <div className="container-fluid projects-helper-item-row" onClick={handleClick}>
            {props.name} <span className="projects-helper-course-row-id"> {props.id}</span>
        </div>
    )
}
