import "../style/login.css";

interface CourseComponentProps {
    key: number;
    id: string;
    name: string;
}

export function CourseComponent(props: CourseComponentProps) {

    return (
        <div className="container-fluid projects-helper-user-data-cont">
            <div className="row projects-helper-user-data-row">
                {props.name} - {props.id}
            </div>
        </div>)
}
