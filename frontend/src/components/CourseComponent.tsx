interface CourseComponentProps {
    key: number;
    id: string;
    name: string;
}

export function CourseComponent(props: CourseComponentProps) {

    return (
        <div className="container-fluid projects-helper-course-row">
            {props.name} <span className="projects-helper-course-row-id"> {props.id}</span>
        </div>
    )
}
