package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 18/01/2015.
 */



public class Links {

    //Ruta de las imagenes
    static String url_images="http://interestingworld.webcindario.com/images/";
    //Eliminar datos
    static String url_delete_images="http://interestingworld.webcindario.com/delete_image.php";
    static String url_delete_comments="http://interestingworld.webcindario.com/delete_comment.php";
    static String url_delete_user="http://interestingworld.webcindario.com/delete_user.php";
    static String url_delete_location="http://interestingworld.webcindario.com/delete_location.php";
    //Añadir datos
    static String url_add_location="http://interestingworld.webcindario.com/insert_location.php";
    static String url_add_image="";
    static String url_add_image_location="http://interestingworld.webcindario.com/insert_photo_location.php";
    static String url_add_location_visited="http://interestingworld.webcindario.com/insert_location_visited.php";
    static String url_add_comment="http://interestingworld.webcindario.com/insert_comment.php";
    static String url_add_user="http://interestingworld.webcindario.com/insert_user.php";
    static String url_add_rating_image="http://interestingworld.webcindario.com/insert_rating_image.php";
    static String url_add_rating_location="http://interestingworld.webcindario.com/insert_rating_location.php";
    //Consultar datos
    static String url_get_locations="http://interestingworld.webcindario.com/consulta_locations.php";
    static String url_get_images="http://interestingworld.webcindario.com/consulta_photos.php";
    static String url_get_locations_visited="http://interestingworld.webcindario.com/consulta_locations_visited.php";
    static String url_get_locations_liked="http://interestingworld.webcindario.com/consulta_locations_liked.php";
    static String url_get_locations_user="http://interestingworld.webcindario.com/consulta_locations_user.php";
    static String url_get_images_user="http://interestingworld.webcindario.com/consulta_photos_user.php";
    static String url_get_images_location="http://interestingworld.webcindario.com/consulta_photos_detail.php";
    static String url_get_map="http://interestingworld.webcindario.com/consulta_map_precise.php";
    static String url_get_comments="http://interestingworld.webcindario.com/consulta_comments.php";
    //Buscar usuario login
    static String url_search_user="http://interestingworld.webcindario.com/search_user.php";
    //Editar
    static String url_edit_user="http://interestingworld.webcindario.com/edit_user.php";
    static String url_update_image_location="http://interestingworld.webcindario.com/update_image_location.php";

    static String getUrl_get_map() {
        return url_get_map;
    }

    static String getUrl_images() {
        return url_images;
    }

    static String getUrl_delete_images() {
        return url_delete_images;
    }

    static String getUrl_delete_comments() {
        return url_delete_comments;
    }

    static String getUrl_delete_user() {
        return url_delete_user;
    }

    static String getUrl_delete_location() {
        return url_delete_location;
    }

    static String getUrl_add_location() {
        return url_add_location;
    }

    static String getUrl_add_image() {
        return url_add_image;
    }

    static String getUrl_add_comment() {
        return url_add_comment;
    }

    static String getUrl_add_user() {
        return url_add_user;
    }

    static String getUrl_get_locations() {
        return url_get_locations;
    }

    static String getUrl_get_images() {
        return url_get_images;
    }

    static String getUrl_get_locations_visited() {
        return url_get_locations_visited;
    }

    static String getUrl_get_locations_liked() {
        return url_get_locations_liked;
    }

    public static String getUrl_add_rating_image() {
        return url_add_rating_image;
    }


    public static String getUrl_get_comments() {
        return url_get_comments;
    }


    public static String getUrl_add_rating_location() {
        return url_add_rating_location;
    }

    public static String getUrl_add_image_location() {
        return url_add_image_location;
    }

    public static String getUrl_add_location_visited() {
        return url_add_location_visited;
    }

    public static String getUrl_get_locations_user() {
        return url_get_locations_user;
    }

    public static String getUrl_get_images_user() {
        return url_get_images_user;
    }

    public static String getUrl_get_images_location() {
        return url_get_images_location;
    }

    public static String getUrl_search_user() {
        return url_search_user;
    }

    public static String getUrl_edit_user() {
        return url_edit_user;
    }

    public static String getUrl_update_image_location() {
        return url_update_image_location;
    }
}
