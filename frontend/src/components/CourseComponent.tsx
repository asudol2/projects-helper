import { useNavigate } from "react-router-dom";

interface CourseComponentProps {
    key: number;
    id: string;
    name: string;
}

export function CourseComponent(props: CourseComponentProps) {
    const navigate = useNavigate();


    const handleClisk = () => {
        navigate("/course/"+props.name+"&"+props.id);
    }
    return (
        <div className="container-fluid projects-helper-item-row" onClick={handleClisk}>
            {props.name} <span className="projects-helper-item-row-id"> {props.id}</span>
        </div>
    )
}
